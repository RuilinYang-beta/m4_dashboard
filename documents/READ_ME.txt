
The map on the dashboard took quite some time and code, so we are pretty proud of that.


To test the system, you can browse to http://farm03.ewi.utwente.nl/Project/
Another option is to run it locally on a tomcat server and browse to http://localhost:8080/Project/
1 Authorization
	to gain access to the system, one's email address needs to be added to the database.
	this can be done using the link:
	http://farm03.ewi.utwente.nl/Project/sql/auth?name={name}&id={id}&mail={emailaddress}
	((OR if ran locally:) http://localhost:8080/Project/rest/sql/auth?name={name}&id={id}&mail={emailaddress})
	IMPORTANT: These should be a POST request, not get
	PLEASE NOTE: the name doesn't really matter, the ID should be unique in the Database, so taking a large random
	integer assures that it wont be in the database. emailaddress will be saved in database as sha256 hash, so are completely private.
	The email address should also be unique in the database, but if its already there, you can already access the webpage.
	After that, you are authorized, so you can log in through using Google on our main page (or any other page).
2 Accessing webapp:
	To open the webapp, use the link http://farm03.ewi.utwente.nl/Project/ (OR if ran locally: http://localhost:8080/Project/)
	if you are logged in with google, it allows you to access the page freely. Feel free to use it any way you see fit but remember:
	adding an environment takes incredebly long. it requires a total of about 1.8 million entries to be copied to our postgresql database,
	resulting in a wait time of about 2 to 2.5 hours. The system will still work while copying, but it's kind of heavy on the internet connection.
3 Viewing pages
	The main dashboard contains pretty selfexplanatory data and map. click anywhere to continue to the filterpage.
	The filterpage allows to find specific statistics for date, company and others.
	The Settings page allows to add an environment (new customer in the system) and to add an employee as Admin.
