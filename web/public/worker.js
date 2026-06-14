// Web Worker that hosts the C++ engine (WASM). Solving runs here so multi-second
// IDA* solves never freeze the 3D cube on the main thread. The worker also streams
// the corner pattern database into the in-memory filesystem on demand.

import createRubiksModule from './rubiks.mjs';

const DB_PATH = '/cornerDepth5V1.txt';
const DB_URL = './cornerDepth5V1.txt';
const DB_BYTES = 50089921; // exact uncompressed size — used for accurate progress

let Module = null;
let cube = null;
let dbReady = false;

async function init() {
  try {
    Module = await createRubiksModule();
    cube = new Module.WebCube();
    postMessage({ type: 'ready' });
  } catch (err) {
    postMessage({ type: 'initError', message: String(err) });
  }
}

async function loadDB() {
  if (dbReady) { postMessage({ type: 'dbReady' }); return; }
  try {
    const resp = await fetch(DB_URL);
    if (!resp.ok) throw new Error('HTTP ' + resp.status);

    const reader = resp.body.getReader();
    const chunks = [];
    let received = 0;
    for (;;) {
      const { done, value } = await reader.read();
      if (done) break;
      chunks.push(value);
      received += value.length;
      postMessage({ type: 'dbProgress', pct: Math.min(100, Math.round((received / DB_BYTES) * 100)) });
    }

    const data = new Uint8Array(received);
    let offset = 0;
    for (const c of chunks) { data.set(c, offset); offset += c.length; }

    Module.FS.writeFile(DB_PATH, data);
    cube.setDbPath(DB_PATH);
    dbReady = true;
    postMessage({ type: 'dbReady' });
  } catch (err) {
    postMessage({ type: 'dbError', message: String(err) });
  }
}

onmessage = async (e) => {
  const msg = e.data;
  if (msg.type === 'loadDB') {
    await loadDB();
    return;
  }
  if (msg.type === 'solve') {
    let solution = 'ERROR';
    let ms = 0;
    try {
      cube.reset();
      if (msg.scramble) cube.applyMoves(msg.scramble);
      const t0 = performance.now();
      solution = cube.solve(msg.algo, msg.maxDepth);
      ms = performance.now() - t0;
    } catch (err) {
      solution = 'ERROR';
    }
    postMessage({ type: 'solved', id: msg.id, solution, ms });
  }
};

init();
