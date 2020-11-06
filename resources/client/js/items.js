"use strict";
function getItemsList() {
    //debugger;
    console.log("Invoked getItemsList()");     //console.log your BFF for debugging client side - also use debugger statement
    const url = "/Items/list";    		// API method on web server will be in Users class, method list
    fetch(url, {
        method: "GET",				//Get method
    }).then(response => {
        return response.json();                 //return response as JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) { //checks if response from the web server has an "Error"
            alert(JSON.stringify(response));    // if it does, convert JSON object to string and alert (pop up window)
        } else {
            formatItemsList(response);          //this function will create an HTML table of the data (as per previous lesson)
        }
    });
}
function formatItemsList(myJSONArray){
    let dataHTML = "";
    for (let item of myJSONArray) {
        dataHTML += "<tr><td>" + item.ItemID + "<td><td>" + item.Name + "<tr><td>";
    }
    document.getElementById("ItemsTable").innerHTML = dataHTML;
}


