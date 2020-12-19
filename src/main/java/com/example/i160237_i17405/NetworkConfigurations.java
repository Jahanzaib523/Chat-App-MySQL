package com.example.i160237_i17405;

public class NetworkConfigurations {
    private static String ipAddress;


    public  static String getRootPath(){
        return "http://" + ipAddress + "/assignment3";
    }

    public static void setIpAddress(String ipAddress) {
        com.example.i160237_i17405.NetworkConfigurations.ipAddress = ipAddress;
    }

    public static String getIpAddress() {
        return ipAddress;
    }

    static String getUrlForInsertingUserDetails(){
        return "http://"+ipAddress+"/assignment3/insertUserDetails.php";
    }

    static String getUrlForGettingUserDetails(){
        return "http://"+ipAddress+"/assignment3/getUserDetails.php";
    }

    static String getUrlForUpdatingUserDetails(){
        return "http://"+ipAddress+"/assignment3/updateUserDetails.php";
    }

    static String getUrlForInsertingMessage(){
        return "http://"+ipAddress+"/assignment3/insertMessages.php";
    }

    static String getUrlForGettingMessages(){
        return "http://"+ipAddress+"/assignment3/getMessages.php";
    }


    public static String getUrlForUpdatingMessages() {
        return "http://"+ipAddress+"/assignment3/updateMessages.php";
    }
}
