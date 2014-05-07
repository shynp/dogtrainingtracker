<?php
	include('smart_resize_image.function.php');
	// Retrieve Post variables
	$name = $_POST['name'];
	$dob = $_POST['dob'];
	//$dob = "1994-10-23";
	$breed = $_POST['breed'];
	$serviceType = $_POST['serviceType'];
	$dogID = $_POST['dogid'];
	
	if (isset($_POST['imageString']))
	{
		$imageString = $_POST['imageString'];
		$image = base64_decode($imageString);
		$imageFileName = "dog_image_".$dogID.".jpg";
		$imgPath = "dog_images".DIRECTORY_SEPARATOR.$imageFileName;
		$file = fopen($imgPath,'w');
		fwrite($file, $image);
		
		// Save thumbnail sized photo
		$imgPathResized = 	"dog_images_thumb".DIRECTORY_SEPARATOR.$imageFileName;
		smart_resize_image($imgPath, 50, 50, false, $imgPathResized, false, false, 100);
	}
	// Connect to server
	$con = new mysqli("mysql.freehostingnoads.net","u239530328_user","cis35o","u239530328_serv", 3306);

	// Add new dog entry
	$stmt = $con->prepare('UPDATE dogs SET name=?, birth_date=?, breed=?, service_type=?, version_number = version_number + 1 WHERE id=?');

	$stmt->bind_param('ssssi', $name, $dob, $breed, $serviceType, intval($dogID));
	$stmt->execute();
	

	
	exit('success');
?>