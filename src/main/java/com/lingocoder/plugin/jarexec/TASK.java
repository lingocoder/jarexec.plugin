package com.lingocoder.plugin.jarexec;


public enum TASK {
    EXEC_JAR("execjar");
    private String taskName;
    private TASK(String taskName){
        this.taskName = taskName;
    }
    public String toString(){
        return taskName;
    }
}