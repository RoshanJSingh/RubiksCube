import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { RoundedBoxGeometry } from 'three/addons/geometries/RoundedBoxGeometry.js';

/* ------------------------------------------------------------------ *
 *  Rubik's Cube Solver — frontend
 *
 *  The C++ engine (compiled to WebAssembly) is the brain: given a
 *  scramble it returns a solution. It runs inside a Web Worker so that
 *  multi-second IDA* solves never freeze the 3D cube. This file handles
 *  rendering, animation, and talking to the worker. Move tokens use
 *  standard notation; each token's (axis, layer, direction) below
 *  matches the C++ move definitions (verified against the bitboard model).
 * ------------------------------------------------------------------ */

const STICKER = {
  px: 0x1457d6, nx: 0x00a651, py: 0xf6f7fb, ny: 0xffd400, pz: 0xd01f3c, nz: 0xff6a00,
};
const BODY_COLOR = 0x0b0c14;

const MOVES = {
  "U":  ['y',  1, -1], "U'": ['y',  1,  1], "U2": ['y',  1, 2],
  "D":  ['y', -1,  1], "D'": ['y', -1, -1], "D2": ['y', -1, 2],
  "R":  ['x',  1, -1], "R'": ['x',  1,  1], "R2": ['x',  1, 2],
  "L":  ['x', -1,  1], "L'": ['x', -1, -1], "L2": ['x', -1, 2],
  "F":  ['z',  1, -1], "F'": ['z',  1,  1], "F2": ['z',  1, 2],
  "B":  ['z', -1,  1], "B'": ['z', -1, -1], "B2": ['z', -1, 2],
};
const FACES = ['U', 'D', 'L', 'R', 'F', 'B'];
const SUFFIX = ['', "'", '2'];
const SP = 1.0;
const HALF = Math.PI / 2;

const ALGO_CFG = {
  ida:   { label: 'IDA*',  min: 3, max: 12, def: 10, db: true,  hint: 'Optimal · corner pattern database · handles deep scrambles.' },
  iddfs: { label: 'IDDFS', min: 3, max: 6,  def: 5,  db: false, hint: 'Optimal · iterative deepening · no database (shallow only).' },
  dfs:   { label: 'DFS',   min: 3, max: 6,  def: 5,  db: false, hint: 'First solution found · bounded depth-first search.' },
};

// ===================================================================
//  Scene
// ===================================================================
const canvas = document.getElementById('cube-canvas');
const renderer = new THREE.WebGLRenderer({ canvas, antialias: true, alpha: true });
renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
renderer.outputColorSpace = THREE.SRGBColorSpace;

const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera(42, 1, 0.1, 100);
camera.position.set(4.6, 4.2, 6.0);

const controls = new OrbitControls(camera, canvas);
controls.enableDamping = true;
controls.dampingFactor = 0.09;
controls.enablePan = false;
controls.minDistance = 5.5;
controls.maxDistance = 13;
controls.autoRotate = true;
controls.autoRotateSpeed = 0.9;
controls.target.set(0, 0, 0);
controls.addEventListener('start', () => { controls.autoRotate = false; });

scene.add(new THREE.AmbientLight(0xffffff, 0.62));
const key = new THREE.DirectionalLight(0xffffff, 1.55);
key.position.set(6, 9, 7);
scene.add(key);
const fill = new THREE.DirectionalLight(0x88aaff, 0.45);
fill.position.set(-7, -3, -5);
scene.add(fill);
const rim = new THREE.DirectionalLight(0xff66cc, 0.25);
rim.position.set(-4, 6, -6);
scene.add(rim);

const cubeGroup = new THREE.Group();
scene.add(cubeGroup);

// ===================================================================
//  Build the 27 cubies
// ===================================================================
const cubies = [];
const bodyMat = new THREE.MeshStandardMaterial({ color: BODY_COLOR, roughness: 0.55, metalness: 0.1 });
const bodyGeo = new RoundedBoxGeometry(0.95, 0.95, 0.95, 4, 0.11);

const makeStickerMat = (color) => new THREE.MeshStandardMaterial({ color, roughness: 0.34, metalness: 0.05 });
const stickerMats = {
  px: makeStickerMat(STICKER.px), nx: makeStickerMat(STICKER.nx),
  py: makeStickerMat(STICKER.py), ny: makeStickerMat(STICKER.ny),
  pz: makeStickerMat(STICKER.pz), nz: makeStickerMat(STICKER.nz),
};
const stickerGeo = {
  x: new RoundedBoxGeometry(0.06, 0.8, 0.8, 3, 0.07),
  y: new RoundedBoxGeometry(0.8, 0.06, 0.8, 3, 0.07),
  z: new RoundedBoxGeometry(0.8, 0.8, 0.06, 3, 0.07),
};

function buildCube() {
  for (let i = -1; i <= 1; i++) {
    for (let j = -1; j <= 1; j++) {
      for (let k = -1; k <= 1; k++) {
        if (i === 0 && j === 0 && k === 0) continue;
        const cubie = new THREE.Group();
        cubie.position.set(i * SP, j * SP, k * SP);
        cubie.userData.home = cubie.position.clone();

        cubie.add(new THREE.Mesh(bodyGeo, bodyMat));

        const addSticker = (geo, mat, pos) => {
          const s = new THREE.Mesh(geo, mat);
          s.position.copy(pos);
          cubie.add(s);
        };
        if (i === 1)  addSticker(stickerGeo.x, stickerMats.px, new THREE.Vector3(0.475, 0, 0));
        if (i === -1) addSticker(stickerGeo.x, stickerMats.nx, new THREE.Vector3(-0.475, 0, 0));
        if (j === 1)  addSticker(stickerGeo.y, stickerMats.py, new THREE.Vector3(0, 0.475, 0));
        if (j === -1) addSticker(stickerGeo.y, stickerMats.ny, new THREE.Vector3(0, -0.475, 0));
        if (k === 1)  addSticker(stickerGeo.z, stickerMats.pz, new THREE.Vector3(0, 0, 0.475));
        if (k === -1) addSticker(stickerGeo.z, stickerMats.nz, new THREE.Vector3(0, 0, -0.475));

        cubeGroup.add(cubie);
        cubies.push(cubie);
      }
    }
  }
}
buildCube();

// ===================================================================
//  Move engine
// ===================================================================
const axisIndex = { x: 0, y: 1, z: 2 };

function selectLayer(axis, layer) {
  const idx = axisIndex[axis];
  const target = layer * SP;
  return cubies.filter(c => Math.abs(c.position.getComponent(idx) - target) < 0.25);
}

function snapPosition(c) {
  c.position.x = Math.round(c.position.x / SP) * SP;
  c.position.y = Math.round(c.position.y / SP) * SP;
  c.position.z = Math.round(c.position.z / SP) * SP;
}

function rotateLayer(axis, layer, angle, duration) {
  return new Promise(resolve => {
    const pivot = new THREE.Group();
    scene.add(pivot);
    const members = selectLayer(axis, layer);
    members.forEach(c => pivot.attach(c));

    const finish = () => {
      pivot.rotation[axis] = angle;
      pivot.updateMatrixWorld(true);
      [...pivot.children].forEach(c => { cubeGroup.attach(c); snapPosition(c); });
      scene.remove(pivot);
      resolve();
    };

    if (duration <= 0) { finish(); return; }

    const start = performance.now();
    const stepFn = (now) => {
      const t = Math.min(1, (now - start) / duration);
      const e = t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
      pivot.rotation[axis] = angle * e;
      if (t < 1) requestAnimationFrame(stepFn);
      else finish();
    };
    requestAnimationFrame(stepFn);
  });
}

const angleOf = (dir) => (dir === 2 ? Math.PI : dir * HALF);

function applyToken(token, duration) {
  const m = MOVES[token];
  if (!m) return Promise.resolve();
  return rotateLayer(m[0], m[1], angleOf(m[2]), duration);
}

function invToken(token) {
  if (token.endsWith('2')) return token;
  return token.endsWith("'") ? token[0] : token + "'";
}

function resetCubeInstant() {
  cubies.forEach(c => { c.position.copy(c.userData.home); c.quaternion.identity(); });
}

function isVisuallySolved() {
  return cubies.every(c => {
    if (c.position.distanceTo(c.userData.home) > 0.08) return false;
    const ang = 2 * Math.acos(Math.min(1, Math.abs(c.quaternion.w)));
    return ang < 0.1;
  });
}

function genScramble(n) {
  const out = [];
  let last = -1;
  for (let i = 0; i < n; i++) {
    let f;
    do { f = Math.floor(Math.random() * 6); } while (f === last);
    last = f;
    out.push(FACES[f] + SUFFIX[Math.floor(Math.random() * 3)]);
  }
  return out;
}

function selfTest() {
  const seq = genScramble(14);
  seq.forEach(t => applyToken(t, 0));
  seq.slice().reverse().forEach(t => applyToken(invToken(t), 0));
  console.log(`[cube self-test] move engine ${isVisuallySolved() ? 'PASS ✓' : 'FAIL ✗'}`);
  resetCubeInstant();
}

// ===================================================================
//  Render loop + resize
// ===================================================================
function resize() {
  const w = canvas.clientWidth, h = canvas.clientHeight;
  if (canvas.width !== w || canvas.height !== h) {
    renderer.setSize(w, h, false);
    camera.aspect = w / h;
    camera.updateProjectionMatrix();
  }
}
function animate() {
  resize();
  controls.update();
  renderer.render(scene, camera);
  requestAnimationFrame(animate);
}
animate();
selfTest();

// ===================================================================
//  Engine worker
// ===================================================================
const el = (id) => document.getElementById(id);
let worker = null;
let engineReady = false;
let solveCounter = 0;
const pendingSolves = new Map();

// db: 'none' | 'loading' | 'ready' | 'error'
let dbState = 'none';
let dbWaiters = [];

function startWorker() {
  try {
    worker = new Worker(new URL('./worker.js', import.meta.url), { type: 'module' });
  } catch (err) {
    console.error('Worker failed to start:', err);
    return false;
  }
  worker.onmessage = (e) => {
    const msg = e.data;
    switch (msg.type) {
      case 'ready':
        engineReady = true;
        onEngineReady();
        break;
      case 'initError':
        console.error('Engine init error:', msg.message);
        onEngineFailed();
        break;
      case 'dbProgress':
        showDbProgress(msg.pct);
        break;
      case 'dbReady':
        dbState = 'ready';
        hideDbProgress();
        dbWaiters.forEach(r => r(true));
        dbWaiters = [];
        break;
      case 'dbError':
        console.error('DB load error:', msg.message);
        dbState = 'error';
        hideDbProgress();
        dbWaiters.forEach(r => r(false));
        dbWaiters = [];
        break;
      case 'solved': {
        const resolve = pendingSolves.get(msg.id);
        if (resolve) { pendingSolves.delete(msg.id); resolve(msg); }
        break;
      }
    }
  };
  worker.onerror = (err) => { console.error('Worker error:', err.message); onEngineFailed(); };
  return true;
}

function ensureDB() {
  if (dbState === 'ready') return Promise.resolve(true);
  if (dbState === 'error') return Promise.resolve(false);
  return new Promise((resolve) => {
    dbWaiters.push(resolve);
    if (dbState === 'none') {
      dbState = 'loading';
      showDbProgress(0);
      worker.postMessage({ type: 'loadDB' });
    }
  });
}

function requestSolve(scramble, algo, maxDepth) {
  return new Promise((resolve) => {
    const id = ++solveCounter;
    pendingSolves.set(id, resolve);
    worker.postMessage({ type: 'solve', id, scramble, algo, maxDepth });
  });
}

// ===================================================================
//  UI
// ===================================================================
const depthSlider = el('depth');
const depthOut = el('depth-out');
const algoHint = el('algo-hint');
const segBtns = [...document.querySelectorAll('.seg-btn')];
const scrambleBtn = el('scramble-btn');
const solveBtn = el('solve-btn');
const resetBtn = el('reset-btn');
const scrambleSeq = el('scramble-seq');
const solutionSeq = el('solution-seq');
const statMoves = el('stat-moves');
const statTime = el('stat-time');
const statAlgo = el('stat-algo');
const loading = el('loading');
const dbLoad = el('db-load');
const dbBarFill = el('db-bar-fill');
const dbPct = el('db-load-pct');

let currentAlgo = 'ida';
let currentScramble = [];
let scrambled = false;
let busy = false;

function showDbProgress(pct) { dbLoad.hidden = false; dbBarFill.style.width = pct + '%'; dbPct.textContent = pct + '%'; }
function hideDbProgress() { dbLoad.hidden = true; }

function applyAlgoConfig(algo) {
  const cfg = ALGO_CFG[algo];
  depthSlider.min = cfg.min;
  depthSlider.max = cfg.max;
  depthSlider.value = cfg.def;
  depthOut.textContent = cfg.def;
  algoHint.textContent = cfg.hint;
  statAlgo.textContent = cfg.label;
}

function setAlgo(algo) {
  if (algo === currentAlgo || busy) return;
  currentAlgo = algo;
  segBtns.forEach(b => b.classList.toggle('active', b.dataset.algo === algo));
  applyAlgoConfig(algo);
  doReset();
  if (ALGO_CFG[algo].db && engineReady) ensureDB(); // prefetch DB in the background
}

depthSlider.addEventListener('input', () => { depthOut.textContent = depthSlider.value; });
segBtns.forEach(b => b.addEventListener('click', () => setAlgo(b.dataset.algo)));

function setBusy(state) {
  busy = state;
  scrambleBtn.disabled = state || !engineReady;
  resetBtn.disabled = state || (!scrambled && currentScramble.length === 0);
  solveBtn.disabled = state || !engineReady || !scrambled;
  depthSlider.disabled = state;
  segBtns.forEach(b => (b.disabled = state));
}

const nextFrame = () => new Promise(r => requestAnimationFrame(() => r()));

function renderSeq(target, tokens, emptyText) {
  target.innerHTML = '';
  if (!tokens.length) { target.innerHTML = `<span class="seq-empty">${emptyText || '—'}</span>`; return; }
  tokens.forEach((t) => {
    const chip = document.createElement('span');
    chip.className = 'move-chip';
    chip.textContent = t;
    target.appendChild(chip);
  });
}

async function playSequence(tokens, duration, seqTarget) {
  const chips = seqTarget ? [...seqTarget.querySelectorAll('.move-chip')] : [];
  for (let i = 0; i < tokens.length; i++) {
    if (chips[i]) chips[i].classList.add('active');
    await applyToken(tokens[i], duration);
    if (chips[i]) { chips[i].classList.remove('active'); chips[i].classList.add('done'); }
  }
}

async function doScramble() {
  if (busy || !engineReady) return;
  setBusy(true);
  scrambleBtn.classList.add('busy');

  resetCubeInstant();
  currentScramble = genScramble(+depthSlider.value);
  renderSeq(scrambleSeq, currentScramble);
  renderSeq(solutionSeq, [], '—');
  statMoves.textContent = '—';
  statTime.textContent = '—';

  await playSequence(currentScramble, 170, scrambleSeq);
  scrambled = true;

  scrambleBtn.classList.remove('busy');
  setBusy(false);
}

async function doSolve() {
  if (busy || !engineReady || !scrambled) return;
  const algo = currentAlgo;
  setBusy(true);
  solveBtn.classList.add('busy');

  if (ALGO_CFG[algo].db) {
    const ok = await ensureDB();
    if (!ok) {
      solveBtn.classList.remove('busy');
      algoHint.textContent = 'Could not load the database. Try IDDFS, or reload the page.';
      setBusy(false);
      return;
    }
  }

  await nextFrame();
  const { solution, ms } = await requestSolve(currentScramble.join(' '), algo, currentScramble.length);
  solveBtn.classList.remove('busy');

  if (solution === 'ERROR') {
    renderSeq(solutionSeq, [], 'solver ran out of memory — try fewer moves');
    statMoves.textContent = '—';
    statTime.textContent = '—';
    setBusy(false);
    return;
  }

  const sol = solution.trim() ? solution.trim().split(/\s+/) : [];
  renderSeq(solutionSeq, sol, 'already solved');
  statMoves.textContent = String(sol.length);
  statTime.textContent = ms < 1000 ? `${Math.round(ms)} ms` : `${(ms / 1000).toFixed(2)} s`;

  await playSequence(sol, 280, solutionSeq);

  if (!isVisuallySolved()) {
    console.warn('Visual cube not solved after playback — snapping to solved state.');
    resetCubeInstant();
  }
  scrambled = false;
  setBusy(false);
}

function doReset() {
  resetCubeInstant();
  currentScramble = [];
  scrambled = false;
  renderSeq(scrambleSeq, [], 'cube is solved');
  renderSeq(solutionSeq, [], '—');
  statMoves.textContent = engineReady ? '0' : '—';
  statTime.textContent = '—';
  setBusy(false);
}

scrambleBtn.addEventListener('click', doScramble);
solveBtn.addEventListener('click', doSolve);
resetBtn.addEventListener('click', () => { if (!busy) doReset(); });

function onEngineReady() {
  loading.classList.add('hidden');
  statMoves.textContent = '0';
  setBusy(false);
  if (ALGO_CFG[currentAlgo].db) ensureDB(); // start fetching the DB up front
}

function onEngineFailed() {
  loading.classList.add('hidden');
  engineReady = false;
  algoHint.textContent = 'Engine unavailable — solving is disabled. You can still explore the cube.';
  statAlgo.textContent = 'offline';
  scrambleBtn.disabled = false; // scrambling is pure JS, still works
}

// ---- boot
applyAlgoConfig(currentAlgo);
if (!startWorker()) onEngineFailed();
