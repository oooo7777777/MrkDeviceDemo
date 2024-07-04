// CourseDetailBean.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

package com.mrk.demo;
import java.util.List;

public class CourseDetailBean {
    private String distance;
    private String crowd;
    private String taboo;
    private String introduce;
    private String part;
    private String equipmentId;
    private String coachId;
    private int speed;
    private String cover;
    private int kcal;
    private String gradeDesc;
    private String name;
    private String equipmentName;
    private String id;
    private Coach coach;
    private int courseTime;
    private List<CourseCatalogue> courseCatalogue;

    public String getDistance() { return distance; }
    public void setDistance(String value) { this.distance = value; }

    public String getCrowd() { return crowd; }
    public void setCrowd(String value) { this.crowd = value; }

    public String getTaboo() { return taboo; }
    public void setTaboo(String value) { this.taboo = value; }

    public String getIntroduce() { return introduce; }
    public void setIntroduce(String value) { this.introduce = value; }

    public String getPart() { return part; }
    public void setPart(String value) { this.part = value; }

    public String getEquipmentId() { return equipmentId; }
    public void setEquipmentId(String value) { this.equipmentId = value; }

    public String getCoachId() { return coachId; }
    public void setCoachId(String value) { this.coachId = value; }

    public int getSpeed() { return speed; }
    public void setSpeed(int value) { this.speed = value; }

    public String getCover() { return cover; }
    public void setCover(String value) { this.cover = value; }

    public int getKcal() { return kcal; }
    public void setKcal(int value) { this.kcal = value; }

    public String getGradeDesc() { return gradeDesc; }
    public void setGradeDesc(String value) { this.gradeDesc = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String value) { this.equipmentName = value; }

    public String getid() { return id; }
    public void setid(String value) { this.id = value; }

    public Coach getCoach() { return coach; }
    public void setCoach(Coach value) { this.coach = value; }

    public int getCourseTime() { return courseTime; }
    public void setCourseTime(int value) { this.courseTime = value; }

    public List<CourseCatalogue> getCourseCatalogue() { return courseCatalogue; }
    public void setCourseCatalogue(List<CourseCatalogue> value) { this.courseCatalogue = value; }
}

// Coach.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

class Coach {
    private String cover;
    private String introduce;
    private String name;
    private String avatar;
    private String title;
    private String coachId;

    public String getCover() { return cover; }
    public void setCover(String value) { this.cover = value; }

    public String getIntroduce() { return introduce; }
    public void setIntroduce(String value) { this.introduce = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String value) { this.avatar = value; }

    public String getTitle() { return title; }
    public void setTitle(String value) { this.title = value; }

    public String getCoachId() { return coachId; }
    public void setCoachId(String value) { this.coachId = value; }
}


class CourseCatalogue {
    private int kcal;
    private int sustainTime;
    private List<CourseLink> courseLinks;
    private String name;
    private int isContinue;
    private String speedDesc;
    private String id;
    private int time;
    private int beginTime;
    private int endTime;
    private String describeInfo;
    private int maxNum;

    public int getKcal() { return kcal; }
    public void setKcal(int value) { this.kcal = value; }

    public int getSustainTime() { return sustainTime; }
    public void setSustainTime(int value) { this.sustainTime = value; }

    public List<CourseLink> getCourseLinks() { return courseLinks; }
    public void setCourseLinks(List<CourseLink> value) { this.courseLinks = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public int getIsContinue() { return isContinue; }
    public void setIsContinue(int value) { this.isContinue = value; }

    public String getSpeedDesc() { return speedDesc; }
    public void setSpeedDesc(String value) { this.speedDesc = value; }

    public String getid() { return id; }
    public void setid(String value) { this.id = value; }

    public int getTime() { return time; }
    public void setTime(int value) { this.time = value; }

    public int getBeginTime() { return beginTime; }
    public void setBeginTime(int value) { this.beginTime = value; }

    public int getEndTime() { return endTime; }
    public void setEndTime(int value) { this.endTime = value; }

    public String getDescribeInfo() { return describeInfo; }
    public void setDescribeInfo(String value) { this.describeInfo = value; }

    public int getMaxNum() { return maxNum; }
    public void setMaxNum(int value) { this.maxNum = value; }
}

// CourseLink.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

class CourseLink {
    private String catalogueId;
    private int sustainTime;
    private int distance;
    private int maxNum;
    private int adviseNum;
    private int minNum;
    private int slopeNum;
    private int kcal;
    private String name;
    private int isExistFlame;
    private String id;
    private int beginTime;
    private int endTime;
    private String crux;

    public String getCatalogueId() { return catalogueId; }
    public void setCatalogueId(String value) { this.catalogueId = value; }

    public int getSustainTime() { return sustainTime; }
    public void setSustainTime(int value) { this.sustainTime = value; }

    public int getDistance() { return distance; }
    public void setDistance(int value) { this.distance = value; }

    public int getMaxNum() { return maxNum; }
    public void setMaxNum(int value) { this.maxNum = value; }

    public int getAdviseNum() { return adviseNum; }
    public void setAdviseNum(int value) { this.adviseNum = value; }

    public int getMinNum() { return minNum; }
    public void setMinNum(int value) { this.minNum = value; }

    public int getSlopeNum() { return slopeNum; }
    public void setSlopeNum(int value) { this.slopeNum = value; }

    public int getKcal() { return kcal; }
    public void setKcal(int value) { this.kcal = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public int getIsExistFlame() { return isExistFlame; }
    public void setIsExistFlame(int value) { this.isExistFlame = value; }

    public String getid() { return id; }
    public void setid(String value) { this.id = value; }

    public int getBeginTime() { return beginTime; }
    public void setBeginTime(int value) { this.beginTime = value; }

    public int getEndTime() { return endTime; }
    public void setEndTime(int value) { this.endTime = value; }

    public String getCrux() { return crux; }
    public void setCrux(String value) { this.crux = value; }
}
