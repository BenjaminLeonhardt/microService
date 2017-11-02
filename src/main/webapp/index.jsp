<!DOCTYPE html>
<html>
<head>
<meta charset=\"utf-8\">
<title>Microservice-Benchmark Startseite</title>
<style>
table {
	display: table;
	border-collapse: separate;
}

table, th, td {
	border: 1px solid black;
	border-spacing: 2px;
	border-color: gray;
}
</style>

</head>
<body>
	<h2>Willkommen zum Microservice-Benchmark</h2>
	<form
		action="http://localhost:8080/microServices/webapi/Transformator/init">
		<br> <br> Um den Benchmark zu starten, füllen Sie bitte alle
		Felder aus und klicken Sie auf Senden. <br> <br>
		<table>
			<tbody>
				<tr>
					<td><b>Titel:</b></td>
					<td><input type="text" name="titel"></td>
					<td>Titel des Auftrags</td>
				</tr>
				<tr>
					<td>Stringanzahl:</td>
					<td><input type="text" name="bis"></td>
					<td>Anzahl der Strings die umgedreht werden sollen. Maximum:
						1.000.000</td>
				</tr>
				<tr>
					<td>Stapelgröße:</td>
					<td><input type="text" name="stapelgroesse"></td>
					<td>Wie viele Strings soll ein Datenpaket beinhalten. Max.
						hängt von der Stringanzahl ab.</td>
				</tr>
				<tr>
					<td>Protokoll:</td>
					<td><select name="protokoll">
							<option value="1">REST</option>
							<option value="2">SOAP</option>
							<option value="3">gRPC</option>
					</select></td>
				</tr>
				<tr>
					<td>Mit UmdrehenMS:</td>
					<td><select name="parameter">
							<option value="1">Nein nur Transformator</option>
							<option value="2">Ja mit UmdrehenMS</option>
					</select></td>
				</tr>
				<tr>
					<td><input type="submit"></td>
					<td></td>
				</tr>
			</tbody>
		</table>
	</form>
	<br>
	<br>
	<br>
	<br>
	<br>
	<h3>Statusanzeigen:</h3>
	<a
		href="http://localhost:8080/microServices/webapi/Transformator/aktiveProzesse">Liste
		aller aktiven Prozesse</a>
	<br>
	<a
		href="http://localhost:8080/microServices/webapi/Transformator/fertigeProzesse">Liste
		aller fertigen Prozesse</a>
	<br>
	<br>
	
	
	
	<h3>Zurücksetzen:</h3>
	<a
		href="http://localhost:8080/microServices/webapi/Transformator/resetAuftragsliste">Liste
		mit Aufträgen zurücksetzen</a>
	<br>
	<a
		href="http://localhost:8080/microServices/webapi/Transformator/resetAbgeschlosseneAuftraege">Liste
		mit fertigen Aufträgen zurücksetzen</a>
	<br>
	
	
	
	<h3>SQL-Datenbank füllen:</h3>
	<a
		href="http://localhost:8080/microServices/webapi/datenbankFuellen/fuellen?dropTable=0">Füllt
		die SQL-Datenbank mit 1.000.000 zufälligen Strings</a>
	<br>
	<a
		href="http://localhost:8080/microServices/webapi/datenbankFuellen/counter">Counter
		als Fortschrittsanzeige</a>
	<br>
	
	
	
	<h3>Tester:</h3>
	<a
		href="http://localhost:8080/microServices/webapi/Persistenz/tester">Tester
		zum Prüfen, ob die Gedrehten Strings in der Datenbank auch korrekt
		sind, und die IDs zu den Passenden original Strings stimmen</a>
	<br>
</body>
</html>
