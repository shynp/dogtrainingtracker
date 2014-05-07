<?php
	include('smart_resize_image.function.php');
	// Retrieve Post variables
	$name = $_POST['name'];
	$dob = $_POST['dob'];
	//$dob = "1994-10-23";
	$breed = $_POST['breed'];
	$serviceType = $_POST['serviceType'];
	
	$imageString = $_POST['imageString'];
	$image = base64_decode($imageString);
	// Connect to server
	$con = new mysqli("mysql.freehostingnoads.net","u239530328_user","cis35o","u239530328_serv", 3306);
	
	// Add a dummy row so the id is automatically produced
	$stmt = $con->prepare("INSERT INTO dogs (name) VALUES ('placeholder')");
	$stmt->execute();
	
	// Get this id (it will be the max value)
	$result = $con->query("SELECT MAX(id) AS max_id FROM dogs");
	$maxID = 0;
	if ($row = $result->fetch_array())
	{
		$maxID = $row['max_id'];
	}
	$dogID = $maxID;
	
	// Save the full sized image
	$imageFileName = "dog_image_".strval($dogID).".jpg";
	$imgPath = "dog_images".DIRECTORY_SEPARATOR.$imageFileName;
	$file = fopen($imgPath,'w');
	fwrite($file, $image);
	

	// Get skills table name
	$skillsTableName = "skills_table_".strval($dogID);
	$sql = "CREATE TABLE ".$skillsTableName." (category_name VARCHAR(60), completed TINYINT DEFAULT 0, planned TINYINT DEFAULT 0, version_number BIGINT DEFAULT 1)";
	$stmt = $con->prepare($sql);
	$stmt->execute();

	// Add new dog entry
	$stmt = $con->prepare("UPDATE dogs SET name=?, skills_table_name=?, birth_date=?, breed=?, service_type=?, image_name=? WHERE id='$dogID'");

	$stmt->bind_param('ssssss', $name, $skillsTableName, $dob, $breed, $serviceType, $imageFileName);
	$stmt->execute();
	
	// Save thumbnail sized photo
	$imgPathResized = 	"dog_images_thumb".DIRECTORY_SEPARATOR.$imageFileName;
	smart_resize_image($imgPath, 50, 50, false, $imgPathResized, false, false, 100);
	
	exit('success');
?>