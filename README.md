# Creating tests for a springboot app using TestContext Framework and Mochito.

Almost every part of the app is covered by tests.

## The tests are focused on three different parts of the app
First group is JUnit tests for all the models, and constructors. And methods within the model classes.
### Second group
Then tests for service layer for the Advertisement, Apartment and Message Services. Tests for adding/editing/removing objects from the database. As well as numerous  other methods in these three services.

### Third Group
Using MockMvc's all controller functionality is tested for the Advertisement, Apartment and Message Controllers.

## The test code is in Proekt/src/test/
