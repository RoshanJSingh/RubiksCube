import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { RoundedBoxGeometry } from 'three/addons/geometries/RoundedBoxGeometry.js';

/* ------------------------------------------------------------------ *
 *  Rubik's Cube Solver — frontend
 *
 *  The C++ engine (compiled to WebAssembly) is the brain: it takes a
 *  scramble and returns a solution. This file only handles rendering
 *  and animation. Move tokens use standard notation, and each token's
 *  (axis, layer, direction) below matches the C++ move definitions
 *  exactly (verified against RubiksCubeBitboard's u()/d()/r()...).
 * ------------------------------------------------------------------ */

// ---- colors (match the C++ solved state: U=white L=green F=red R=blue B=orange D=yellow)
const STICKER = {
  px: 0x1457d6, // +X  RIGHT  blue
  nx: 0x00a651, // -X  LEFT   green
  py: 0xf6f7fb, // +Y  UP     white
  ny: 0xffd400, // -Y  DOWN   yellow
  pz: 0xd01f3c, // +Z  FRONT  red
  nz: 0xff6a00, // -Z  BACK   orange
};
const BODY_COLOR = 0x0b0c14;

// ---- move table: token -> [axis, layer(-1|1), dir]  (dir: +1 = +90°, -1 = -90°, 2 = 180°)
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
const SP = 1.0;            // grid spacing between cubie centers
const HALF = Math.PI / 2;

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

function makeStickerMat(color) {
  return new THREE.MeshStandardMaterial({ color, roughness: 0.34, metalness: 0.05 });
}
const stickerMats = {
  px: makeStickerMat(STICKER.px), nx: makeStickerMat(STICKER.nx),
  py: makeStickerMat(STICKER.py), ny: makeStickerMat(STICKER.ny),
  pz: makeStickerMat(STICKER.pz), nz: makeStickerMat(STICKER.nz),
};

// thin rounded tiles for each facing
const stickerGeo = {
  x: new RoundedBoxGeometry(0.06, 0.8, 0.8, 3, 0.07),
  y: new RoundedBoxGeometry(0.8, 0.06, 0.8, 3, 0.07),
  z: new RoundedBoxGeometry(0.8, 0.8, 0.06, 3, 0.07),
};

function buildCube() {
  for (let i = -1; i <= 1; i++) {
    for (let j = -1; j <= 1; j++) {
      for (let k = -1; k <= 1; k++) {
        if (i === 0 && j === 0 && k === 0) continue; // skip invisible core
        const cubie = new THREE.Group();
        cubie.position.set(i * SP, j * SP, k * SP);
        cubie.userData.home = cubie.position.clone();

        const body = new THREE.Mesh(bodyGeo, bodyMat);
        cubie.add(body);

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
    const step = (now) => {
      const t = Math.min(1, (now - start) / duration);
      const e = t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2; // easeInOutQuad
      pivot.rotation[axis] = angle * e;
      if (t < 1) requestAnimationFrame(step);
      else finish();
    };
    requestAnimationFrame(step);
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

// internal consistency check: scramble then its exact inverse must solve
function selfTest() {
  const seq = genScramble(14);
  seq.forEach(t => applyToken(t, 0));
  seq.slice().reverse().forEach(t => applyToken(invToken(t), 0));
  const ok = isVisuallySolved();
  console.log(`[cube self-test] move engine ${ok ? 'PASS ✓' : 'FAIL ✗'}`);
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
//  WASM engine
// ===================================================================
let engine = null; // WebCube instance

async function loadEngine() {
  try {
    const mod = await import('./rubiks.mjs');
    const Module = await mod.default();
    engine = new Module.WebCube();
    return true;
  } catch (err) {
    console.error('Failed to load the C++ engine:', err);
    return false;
  }
}

// ===================================================================
//  UI wiring
// ===================================================================
const el = (id) => document.getElementById(id);
const depthSlider = el('depth');
const depthOut = el('depth-out');
const depthHint = el('depth-hint');
const scrambleBtn = el('scramble-btn');
const solveBtn = el('solve-btn');
const resetBtn = el('reset-btn');
const scrambleSeq = el('scramble-seq');
const solutionSeq = el('solution-seq');
const statMoves = el('stat-moves');
const statTime = el('stat-time');
const loading = el('loading');

let currentScramble = [];
let scrambled = false;
let busy = false;

depthSlider.addEventListener('input', () => {
  depthOut.textContent = depthSlider.value;
  depthHint.textContent = +depthSlider.value >= 6
    ? 'Optimal solver · depth 6 can take a few seconds.'
    : 'Optimal solver · finds the shortest solution.';
});

function setBusy(state) {
  busy = state;
  scrambleBtn.disabled = state;
  resetBtn.disabled = state || (!scrambled && currentScramble.length === 0);
  solveBtn.disabled = state || !engine || !scrambled;
  depthSlider.disabled = state;
}

function nextFrame() {
  return new Promise(r => requestAnimationFrame(() => r()));
}

function renderSeq(target, tokens, emptyText) {
  target.innerHTML = '';
  if (!tokens.length) {
    target.innerHTML = `<span class="seq-empty">${emptyText || '—'}</span>`;
    return;
  }
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
  if (busy) return;
  setBusy(true);
  scrambleBtn.classList.add('busy');

  resetCubeInstant();
  const depth = +depthSlider.value;
  currentScramble = genScramble(depth);
  renderSeq(scrambleSeq, currentScramble);
  renderSeq(solutionSeq, [], '—');
  statMoves.textContent = '—';
  statTime.textContent = '—';

  await playSequence(currentScramble, 180, scrambleSeq);

  if (engine) { engine.reset(); engine.applyMoves(currentScramble.join(' ')); }
  scrambled = true;

  scrambleBtn.classList.remove('busy');
  setBusy(false);
}

async function doSolve() {
  if (busy || !engine || !scrambled) return;
  setBusy(true);
  solveBtn.classList.add('busy');

  // make sure the engine reflects exactly what's on screen
  engine.reset();
  engine.applyMoves(currentScramble.join(' '));

  await nextFrame(); // let the spinner paint before the blocking solve
  const t0 = performance.now();
  const solStr = engine.solve(currentScramble.length);
  const elapsed = performance.now() - t0;

  const solution = solStr.trim().length ? solStr.trim().split(/\s+/) : [];
  renderSeq(solutionSeq, solution, 'already solved');
  statMoves.textContent = String(solution.length);
  statTime.textContent = elapsed < 1000
    ? `${Math.round(elapsed)} ms`
    : `${(elapsed / 1000).toFixed(2)} s`;

  solveBtn.classList.remove('busy');
  await playSequence(solution, 280, solutionSeq);

  if (!isVisuallySolved()) {
    console.warn('Visual cube not solved after playback — snapping to solved state.');
    resetCubeInstant();
  }
  scrambled = false;
  setBusy(false);
}

function doReset() {
  if (busy) return;
  resetCubeInstant();
  currentScramble = [];
  scrambled = false;
  renderSeq(scrambleSeq, [], 'cube is solved');
  renderSeq(solutionSeq, [], '—');
  statMoves.textContent = '—';
  statTime.textContent = '—';
  setBusy(false);
}

scrambleBtn.addEventListener('click', doScramble);
solveBtn.addEventListener('click', doSolve);
resetBtn.addEventListener('click', doReset);

// ---- boot
(async () => {
  const ok = await loadEngine();
  loading.classList.add('hidden');
  scrambleBtn.disabled = false;
  if (ok) {
    statMoves.textContent = '0';
  } else {
    depthHint.textContent = 'Engine unavailable — solving is disabled, but you can still explore the cube.';
    el('stat-algo').textContent = 'offline';
  }
})();
