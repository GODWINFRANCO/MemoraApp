<?php
include("connection.php");



// Creating a query to select the new fields from the database
$stmt = $con->prepare("SELECT id,caregivername,caregiverphone,caregiveraddress, caregiverfee, caregiverdob, caregivermail, caregiverpass, caregivergender,caregiverpfp FROM caregiverregister ");

// Executing the query
$stmt->execute();

// Binding result to the query
$stmt->bind_result($id,$caregivername,$caregiverphone,$caregiveraddress,$caregiverfee,$caregiverdob,$caregivermail,$caregiverpass,$caregivergender,$caregiverpfp);

$p = array();

// Fetching each row and adding it to the array
while($stmt->fetch()) {
    $temp = array();
    $temp['id'] = $id;
    $temp['caregivername'] = $caregivername;
    $temp['caregiverphone'] = $caregiverphone;
    $temp['caregiveraddress'] = $caregiveraddress;
    $temp['caregiverfee'] = $caregiverfee;
    $temp['caregiverdob'] = $caregiverdob;
    $temp['caregivermail'] = $caregivermail;
    $temp['caregiverpass'] = $caregiverpass;
    $temp['caregivergender'] = $caregivergender;
    $temp['caregiverpfp'] = $caregiverpfp;
    
  
    array_push($p, $temp);
}

// Returning the result as a JSON-encoded array
echo json_encode($p);
?>
