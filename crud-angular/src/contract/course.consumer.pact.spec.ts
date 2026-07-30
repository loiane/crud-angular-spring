import path from 'node:path';

import { PactV3, MatchersV3 } from '@pact-foundation/pact';

const { eachLike, integer, string } = MatchersV3;

const provider = new PactV3({
  consumer: 'crud-angular',
  provider: 'crud-spring',
  dir: path.resolve(process.cwd(), 'pacts')
});

describe('CoursesService contract', () => {
  it('GET /api/courses returns a page of courses', () => {
    provider
      .given('three courses exist')
      .uponReceiving('a request for the first page of courses')
      .withRequest({
        method: 'GET',
        path: '/api/courses',
        query: { page: '0', pageSize: '10' }
      })
      .willRespondWith({
        status: 200,
        headers: { 'Content-Type': 'application/json' },
        body: {
          totalElements: integer(3),
          totalPages: integer(1),
          courses: eachLike({
            _id: integer(1),
            name: string('Angular'),
            category: string('Front-end')
          })
        }
      });

    return provider.executeTest(async mockServer => {
      // In the Angular app this call goes through CoursesService.list();
      // here we hit the Pact mock server directly to record the interaction.
      const response = await fetch(`${mockServer.url}/api/courses?page=0&pageSize=10`);
      expect(response.status).toBe(200);

      const body = await response.json();
      expect(body.courses[0]._id).toBeDefined();
      expect(body.courses[0].name).toBeDefined();
      expect(body.courses[0].category).toBeDefined();
    });
  });
});
