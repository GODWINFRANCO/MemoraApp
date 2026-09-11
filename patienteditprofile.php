<?php
include("connection.php");
$id =$_POST["id"];
$patientname =$_POST["patientname"];
$patientmail =$_POST["patientmail"];
$patientphone  =$_POST["patientphone"];
$patientaddress  =$_POST["patientaddress"];
$patientpass =$_POST["patientpass"];
$patientdob =$_POST["patientdob"];

$query="update patientregister set patientname='$patientname',patientmail='$patientmail',patientphone='$patientphone',patientaddress='$patientaddress',patientpass='$patientpass',patientdob='$patientdob' where id='$id'";
$result=mysqli_query($con,$query);
if($result){
    $response["status"]="1";
    $response["message"]=" updation successful";
}
else{
    $response["status"]="0";
    $response["message"]="updation failed";
}
echo json_encode($response);
?>