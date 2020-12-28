<?php 

error_reporting(E_ALL);

ini_set("display_errors", 1);


$idarray = $_GET['idarray'];

$fileDir = "data";
$fullPath = $fileDir."/".'o.m4a';   
$length = filesize($fullPath);

header("Content-Type: application/octet-stream");
header("Content-Length: $length");
header("Content-Disposition: attachment; filename=".iconv('utf-8','euc-kr','o.m4a'));
header("Content-Transfer-Encoding: binary");

$fh = fopen($fullPath, "r");
fpassthru($fh);


// header('Content-Type: application/json; charset=utf8');
// $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
// echo $json;


?>