# MessengerMesh Frontend

Run locally:

1. Install dependencies: npm ci
2. Start dev server: npm run dev (http://localhost:3000)

Build for production:

1. npm run build
2. docker build -t messengermesh-frontend .

Notes:
- This frontend uses Web Crypto API for key generation and encryption. Private keys are stored encrypted in IndexedDB by the registration flow.
- The dev server proxies /api and /ws to http://localhost:8080 (see vite.config.ts)
