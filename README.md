Runs on H2 in-memory database.

1. Create USER role<br />
   POST http://localhost:8005/api/v1/roles/create -b {"name": "USER"}

2. Create a user (USER role will be automatically assigned)<br />
   POST http://localhost:8005/api/v1/auth/signup -b {"email": "some@email.com", "password": "Yourpassword123!", "fullName": "Full Name"}

3. Reset password<br />
   PUT http://localhost:8005/api/v1/auth/reset -b {"email": "some@email.com", "oldPassword": "Yourpassword123!", "newPassword": "Yourpassword123@"}

4. Login<br />
   POST http://localhost:8005/api/v1/auth/login -b {"email": "some@email.com", "password": "Yourpassword123!"}<br />
   Will receive a header "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhYUBhYS5jb20iLCJpYXQiOj" (use this in further API calls)

5. Get info about your user<br />
   GET http://localhost:8005/api/v1/users/self -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhYUBhYS5jb20iLCJpYXQiOj" (use token from login)

6. Create a blog item (no authentication is developed for this one)<br />
   POST http://localhost:8005/api/v1/blogs -b {"title": "Some title", "htmlContent": "some html content here..."}

7. Get all blogs<br />
   GET http://localhost:8005/api/v1/blogs (optional query params ?page=0&size=10)

8. Get a single blog<br />
   GET http://localhost:8005/api/v1/blogs/<id>
