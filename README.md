# Horizon
A blog app.
## Idea:
This is a blog app, where the user can post about various topics and others can read it, like it, comment on it and bookmark it.
While posting a new blog, an image is required along with title and content of the blog. The design of the app is similar to Instagram.

## Architecture pattern:
- MVVM Architecture with a single Viewmodel and Repository.
## Libraries and frameworks used:
- Dagger Hilt - for dependency injection.
- Navigation Component - Main Activity hosts several fragments.
- Coil - for image  loading.
- Paging 3 - for pagination of posts.
- Image cropper library by the artofdev - for cropping images while editting profile image.
## Backend:
Firebase is used as the backend of this app.
- Firebase Firestore.
- Firebase Storage.
- Firebase Authentication
## Screenshots and user flow:
### Signup and Login Screen:
<img src="https://github.com/kshitijskumar/Horizon/blob/main/Screenshots/SignupScreen.jpg" height=500> &nbsp;&nbsp;
<img src="https://github.com/kshitijskumar/Horizon/blob/main/Screenshots/LoginScreen.jpg" height=500>&nbsp;&nbsp;
### All posts Screen:
<img src="https://github.com/kshitijskumar/Horizon/blob/main/Screenshots/AllPosts.jpg" height=500>&nbsp;&nbsp;
### Logged in user profile:
<img src="https://github.com/kshitijskumar/Horizon/blob/main/Screenshots/UserProfile.jpg" height=500>&nbsp;&nbsp;
### Another user profile:
<img src="https://github.com/kshitijskumar/Horizon/blob/main/Screenshots/AnotherUser.jpg" height=500>&nbsp;&nbsp;
### Comments on blogs:
<img src="https://github.com/kshitijskumar/Horizon/blob/main/Screenshots/Comments.jpg" height=500>&nbsp;&nbsp;
## Note:
- Search feature is not implemented yet.
- Followers following feature can be added.
- Notifications and Firebase Cloud messaging can be added.
