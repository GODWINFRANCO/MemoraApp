
<?php
include("connection.php");

$caregivermail=$_POST["caregivermail"];
$caregiverpass=$_POST["caregiverpass"];
$query="SELECT * FROM caregiverregister WHERE caregivermail='$caregivermail' && caregiverpass='$caregiverpass'";
$result=mysqli_query($con,$query);
$row=mysqli_fetch_row($result);
if(mysqli_num_rows($result)>0)
{
    $response["status"]="1";
    $response["message"]="Login Successful";
    $response["id"]=$row[0];
    $response["caregivername"]=$row[1];
    $response["caregiverphone"]=$row[2];
    $response["caregiveraddress"]=$row[3];
    $response["caregiverfee"]=$row[4];
    $response["caregiverdob"]=$row[5];
    $response["caregivermail"]=$row[6];
    $response["caregiverpass"]=$row[7];
    $response["caregivergender"]=$row[8];
    $response["caregiverpfp"]=$row[9];


 

}
else
{
    $response["status"]="0";
    $response["message"]="Login failed";
    $response["id"]="";
    $response["caregivername"]="";
    $response["caregiverphone"]="";
    $response["caregiveraddress"]="";
    $response["caregiverfee"]="";
    $response["caregiverdob"]="";
    $response["caregivermail"]="";
    $response["caregiverpass"]="";
    $response["caregivergender"]="";
    $response["caregiverpfp"]="";
}
echo json_encode($response);
?>