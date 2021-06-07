<?php



$data = array();
$data_file = fopen("led_data.json", w);

$data["kolumna"] = (int)($_POST['kolumna']);
$data["wiersz"] = (int)($_POST['wiersz']);
$data["kolor"] = ($_POST['kolor']);



$data_json = json_encode($data);
fwrite($data_file, $data_json);
fclose($data_file);


shell_exec('sudo ./diody.py'); 


?>