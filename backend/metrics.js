// metrics.js — add this file to your backend
import client from 'prom-client';

// ✅ Automatically collects: CPU, memory, event loop lag,
//    heap size, active handles — all the good stuff
const collectDefaultMetrics = client.collectDefaultMetrics;
collectDefaultMetrics({ timeout: 5000 });

// ✅ Export registry so your route can use it
export default client.register;