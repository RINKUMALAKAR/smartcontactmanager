/** / console.log("This is our script console");
const toggleSidebar = () => {
    if ($(".sidebar").is(":visible")) {
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    } else {
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};*/


const toggleSidebar = () => {
    $(".sidebar").toggleClass("visible");
    if ($(".sidebar").hasClass("visible")) {
        $(".content").css("margin-left", "15%"); // Adjust to match sidebar width
    } else {
        $(".content").css("margin-left", "0");
    }
};


