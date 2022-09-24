package com.yatik.statussaver;

public class ImageData {
    private int leftImageId;
    private int rightImageId;

    public ImageData(int leftImageId, int rightImageId){
        this.leftImageId = leftImageId;
        this.rightImageId = rightImageId;
    }

    public int getLeftImageId(){
        return leftImageId;
    }

    public int getRightImageId(){
        return rightImageId;
    }
}
