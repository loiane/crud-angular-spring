import { defineConfig } from 'vitest/config';

// Pact consumer tests run in Node (they spin up a native mock server) and are
// intentionally kept separate from the Angular/jsdom unit tests. Their only job
// is to generate the pact file under crud-angular/pacts.
export default defineConfig({
  test: {
    globals: true,
    environment: 'node',
    include: ['src/contract/**/*.pact.spec.ts']
  }
});
