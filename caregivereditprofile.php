
<?php
include("connection.php");
$id =$_POST["id"];
$caregivername=$_POST["caregivername"];
$caregivermail=$_POST["caregivermail"];
$caregiverphone=$_POST["caregiverphone"];
$caregiveraddress=$_POST["caregiveraddress"];
$caregiverfee=$_POST["caregiverfee"];
$caregiverpass=$_POST["caregiverpass"];
$caregiverdob=$_POST["caregiverdob"];

$query="update caregiverregister set caregivername='$caregivername',caregivermail='$caregivermail',caregiverphone='$caregiverphone',caregiveraddress='$caregiveraddress',caregiverfee='$caregiverfee',caregiverpass='$caregiverpass',caregiverdob='$caregiverdob' where id='$id'";
$result=mysqli_query($con,$query);
if($result){
    $response["status"]="1";
    $response["message"]="updation successful";
}
else{
    $response["status"]="0";
    $response["message"]="updation failed";
}
echo json_encode($response);
?>