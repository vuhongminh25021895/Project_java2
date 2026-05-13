Startup

The app starts in AuctionApp.java (line 10).
It stores the main JavaFX stage in SceneManager, then loads the LOGIN scene.
Scene loading and switching are handled centrally in SwitchSceneControll.java (line 9), which maps:

LOGIN -> Login.fxml (line 8)

REGISTER -> Register.fxml (line 10)

AUCTION_LIST -> AutionList.fxml




Sign-Up Flow

From the login screen, clicking “Don’t have an account?” runs SwitchToSignUp() in LoginController.java (line 44), which switches to the register screen.
The register screen UI is in Register.fxml (line 10).
The logic is in RegisterController.java (line 15).
When the user clicks Sign up, the controller checks:

all fields are filled

confirm password matches

password has at least 6 characters, with letters and numbers

email contains @



If validation passes, the controller creates a request and sends it through RegisterService.java (line 15).
The client calls POST /api/auth/register on the server.
On the server, RegisterController.java (line 19) receives the request.
The server checks:

request fields are not blank

username does not already exist



DB access is done in UserDao.java (line 10), which inserts into users(username, email, password, role).
If successful, the client shows a success alert and returns to the login screen.

Sign-In Flow

The login screen UI is in Login.fxml (line 8).
The logic is in LoginController.java (line 13).
When the user clicks Sign In, the controller checks that username and password are not empty.
It sends the credentials through LoginService.java (line 15).
The client calls POST /api/auth/login.
On the server, RegisterController.java (line 48) handles login.
The server asks UserDao.java (line 44) to find a matching username/password.
If credentials are correct, the client switches to the auction list scene.
If not, it shows an error alert.

Backend Boot Flow

The backend starts from ServerApplication.java (line 1).
Spring Boot exposes the REST API under /api/auth.
The DAO uses the DB connection from DatabaseConnection to talk to MySQL.

One important assumption: the users table must now contain a role column, because the old phone-based register flow is no longer used.

If you want, I can also draw this as a simple flowchart.
