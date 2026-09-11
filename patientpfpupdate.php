<?php
header('Content-Type: application/json');
// Enable error reporting for debugging (remove in production)
ini_set('display_errors', 0); // Disable display to avoid JSON corruption
ini_set('log_errors', 1);
error_reporting(E_ALL);

include("connection.php");
$response = array('status' => '0', 'message' => 'Unknown error');
if (!isset($_POST['id']) || !isset($_FILES['filename'])) {
    $response['status'] = '0';
    $response['message'] = 'Missing user ID or image file';
    echo json_encode($response);
    exit;
}
$id = mysqli_real_escape_string($con, $_POST['id']);
if (!is_numeric($id)) {
    $response['status'] = '0';
    $response['message'] = 'Invalid user ID';
    echo json_encode($response);
    exit;
}
$originalImgName = basename($_FILES['filename']['name']);
$tempName = $_FILES['filename']['tmp_name'];
$folder = "patientpfpfolder/";
$allowedTypes = array('image/jpeg', 'image/png', 'image/gif');
$fileMimeType = mime_content_type($tempName);
if (!in_array($fileMimeType, $allowedTypes)) {
    $response['status'] = '0';
    $response['message'] = 'Invalid file type. Only JPEG, PNG, and GIF are allowed';
    echo json_encode($response);
    exit;
}
$fileSize = $_FILES['filename']['size'];
$maxFileSize = 5 * 1024 * 1024;
if ($fileSize > $maxFileSize) {
    $response['status'] = '0';
    $response['message'] = 'File size exceeds 5MB limit';
    echo json_encode($response);
    exit;
}
$fileExtension = pathinfo($originalImgName, PATHINFO_EXTENSION);
$newFileName = $id . '_' . time() . '.' . $fileExtension;
$targetPath = $folder . $newFileName;
if (!is_dir($folder)) {
    if (!mkdir($folder, 0755, true)) {
        $response['status'] = '0';
        $response['message'] = 'Failed to create directory';
        echo json_encode($response);
        exit;
    }
}
if (move_uploaded_file($tempName, $targetPath)) {
    $query = "UPDATE patientregister SET patientpfp='$newFileName' WHERE id='$id'";
    if (mysqli_query($con, $query)) {
        $response['status'] = '1';
        $response['message'] = 'file uploaded successfully';
    } else {
        unlink($targetPath);
        $response['status'] = '0';
        $response['message'] = 'Data insertion failed: ' . mysqli_error($con);
    }
} else {
    $response['status'] = '0';
    $response['message'] = 'File moving failed';
}
mysqli_close($con);
echo json_encode($response);
?>