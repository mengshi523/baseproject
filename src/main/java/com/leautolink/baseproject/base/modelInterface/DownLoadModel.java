package com.leautolink.baseproject.base.modelInterface;


import com.leautolink.baseproject.base.service.DownLoaderTask;

import java.util.List;

/**
 * File description
 * Created by @author${shimeng}  on @date14/3/9.
 */

public interface DownLoadModel extends BaseModel{
    List<DownLoaderTask> getTaskByType(String type);
    void startTask(DownLoaderTask task);
    void cancelTask(DownLoaderTask task);
    void removeTask(DownLoaderTask task);
    void startAll();
    List<DownLoaderTask> getAllTask();
    DownLoaderTask getTask(int type);
    String getMaxVersion();
    void addTask(String firmware, String url, String name, String md5, String version);
}
