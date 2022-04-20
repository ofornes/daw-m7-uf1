var phrase = 'SuperSecret.';

var users = {
    admin: '40a12d2b7f546c624461b26bd41573e697e91466dc80ef42acb46685a41b961648422e4529fcbef2fdaf79c7edfbc5e737bed9c224d93ecd26a7f5e028bfa3ed', //admin - admin
    pepe: 'c63c4194ea7ab967c7a951a2f784d794318de97710e74bd6d3dcfd680058aecc941973c52e0f74e28aca2840db5a61fb64bfbf974037c34dfb94ebe1b4c860aa', // pepe - 1234
    manolo: '3acd3650d01ddf7b2c5fd3488997982684da62c9318a54ee239d5a1f3db72e90b548f10489888ba0da2f0384fa161f319b7d707f2f89cdbe1cc1b6d9ed192fd8', //manolo - asdf
};
var request = new XMLHttpRequest();
request.open('GET', 'https://www.thecocktaildb.com/api/json/v1/1/filter.php?a=Non_Alcoholic');
request.send();
request.onload = ()=>{
	var cocktails = JSON.parse(request.response);
}

/* Aquest tros de codi comentat genera l'array de cocktails

$fitxer = file_get_contents('https://www.thecocktaildb.com/api/json/v1/1/filter.php?a=Non_Alcoholic');
$fitxer = json_decode($fitxer);
foreach($fitxer->drinks as $drink){
    $detail = file_get_contents('https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i='.$drink->idDrink);
    $detail = json_decode($detail);
    //var_dump($detail);
    $ingredients = '';
    $detail->drinks[0]->strMeasure1 != NULL ? $ingredients .= $detail->drinks[0]->strMeasure1 .' '. $detail->drinks[0]->strIngredient1:NULL;
    $detail->drinks[0]->strMeasure2 != NULL ? $ingredients .= ', '.$detail->drinks[0]->strMeasure2 .' '. $detail->drinks[0]->strIngredient2:NULL;
    $detail->drinks[0]->strMeasure3 != NULL ? $ingredients .= ', '.$detail->drinks[0]->strMeasure3 .' '. $detail->drinks[0]->strIngredient3:NULL;
    $detail->drinks[0]->strMeasure4 != NULL ? $ingredients .= ', '.$detail->drinks[0]->strMeasure4 .' '. $detail->drinks[0]->strIngredient4:NULL;
    $detail->drinks[0]->strMeasure5 != NULL ? $ingredients .= ', '.$detail->drinks[0]->strMeasure5 .' '. $detail->drinks[0]->strIngredient5:NULL;   
    $detail->drinks[0]->strMeasure6 != NULL ? $ingredients .= ', '.$detail->drinks[0]->strMeasure6 .' '. $detail->drinks[0]->strIngredient6:NULL;
    $detail->drinks[0]->strMeasure7 != NULL ? $ingredients .= ', '.$detail->drinks[0]->strMeasure7 .' '. $detail->drinks[0]->strIngredient7:NULL;
    $detail->drinks[0]->strMeasure8 != NULL ? $ingredients .= ', '.$detail->drinks[0]->strMeasure8 .' '. $detail->drinks[0]->strIngredient8:NULL;
    $detail->drinks[0]->strMeasure9 != NULL ? $ingredients .= ', '.$detail->drinks[0]->strMeasure9 .' '. $detail->drinks[0]->strIngredient9:NULL;
    $detail->drinks[0]->strMeasure10 != NULL ? $ingredients .= ', '.$detail->drinks[0]->strMeasure10 .' '. $detail->drinks[0]->strIngredient10:NULL;

    $array = array(
        'idDrink' => $drink->idDrink,
        'strDrink' => $detail->drinks[0]->strDrink,
        'strDrinkThumb' => $detail->drinks[0]->strDrinkThumb,
        'strIngredients' => $ingredients,
        'strInstructions' =>$detail->drinks[0]->strInstructions
    );
    $cocktails[] = $array;
}
*/

