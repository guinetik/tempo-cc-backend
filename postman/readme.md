# NEWMAN + POSTMAN INTEGRATED TEST RUN

This small module leverages NodeJS to execute Postman collections and tests with the Newman package. 

The index.js script will run mvn install com the springboot app, then it will start the app on a detached process.

Once the app has started gracefully, it will run newman with the collection roles.postman_collection.json using data.json as inputs.

These tests attempt to create a new role and assign a user to it, as well as reading roles and memberships.

To run use, install with npm:

`npm install`

Then start as a node js app:

`npm start`

Please check out the gif for a demo:

![](postman_newman_demo.gif)

If you want to run java separately and use this to run only newman, use:

`npm run newman`

This assumes you have localhost:8080 running already.

Tested with Nodejs v17.9.0 on Windows