
function getBasket() {
    //debugger;
    console.log("Invoked getBasket()");     //console.log your BFF for debugging client side
    //const UserID = document.getElementById("UserID").value;  //get the UserId from the HTML element with id=UserID
    let UserID = 1; 			  //You could hard code it if you have problems
    //debugger;				  //debugger statement to allow you to step through the code in console dev F12
    //const url = "/checkout/get/";       // API method on webserver
    let OrderID = 0;
    //fetch(url + UserID, {                // UserID as a path parameter
    //    method: "GET",
    //}).then(response => {
    //    return response.json();                         //return response to JSON
    //}).then(response => {                                   //something here
     //   if (response.hasOwnProperty("Error")) {         //checks if response from server has an "Error"
     //       alert(JSON.stringify(response)b[);            // if it does, convert JSON object to string and alert
     //   } else {
            //document.getElementById("DisplayOneUser").innerHTML = response.UserID + " " + response.UserName;  //output data
      //      OrderID = response.OrderID;
    //    }
    //});
    //debugger;
    console.log(UserID);
    const url1 = "/checkout/getItems/";
    let itemsHTML = "";
    fetch(url1 + UserID, {                // UserID as a path parameter
        method: "GET",
    }).then(response => {
        return response.json();                         //return response to JSON
    }).then(response => {                                   //something here
        if (response.hasOwnProperty("Error")) {         //checks if response from server has an "Error"
            alert(JSON.stringify(response));            // if it does, convert JSON object to string and alert
        } else {
            for (let items of response) {

              itemsHTML += items.name + " " + items.UserId;
            }
        }
        document.getElementById("DisplayOneUser").innerHTML = itemsHTML;  //output data
        //alert(JSON.stringify(response));
    });

}