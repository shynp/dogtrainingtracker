<?php
	
	// Retrieve Post variables
	$name = $_POST['name'];
	$dob = $_POST['dob'];
	$breed = $_POST['breed'];
	$serviceType = $_POST['serviceType'];
	
	$imageString = $_POST['imageString'];
	$image = base64_decode($imageString);
	
	// Connect to server
	$con = mysqli_connect("mysql.freehostingnoads.net","u239530328_user","cis35o","u239530328_serv", 3306);
	
	// Save the image
	$maxID = mysqli_query($con, "SELECT MAX(id) AS id FROM dogs");
	exit(strval($id));
	$dogID = $maxID + 1;
	$imageFileName = "dog_image_".strval($dogID).".jpg";
	$imgPath = "dog_images".DIRECTORY_SEPARATOR.$imageFileName;
	$file = fopen($imgPath,'w');
	fwrite($file, $image);
	
	// Get skills table name
	$skillsTableName = "skills_table_".strval($dogID);

	// Add new dog entry
	$stmt = $con->prepare('INSERT INTO dogs (name, skills_table_name, birth_date, breed, service_type, image_name) VALUES (?, ?, ?, ?, ?, ?)');

	$stmt->bind_param('ssssss', $name, $skillsTableName, $dob, $breed, $serviceType, $imageFileName);
	$stmt->execute();
	exit('success');
?>