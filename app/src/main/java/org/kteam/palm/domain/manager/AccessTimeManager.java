package org.kteam.palm.domain.manager;

import org.apache.log4j.Logger;
import org.kteam.common.utils.DateUtils;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.domain.repository.AccessTimeRespository;
import org.kteam.palm.domain.repository.BaseDataRepository;
import org.kteam.palm.domain.repository.impl.AccessTimeRepositoryImpl;
import org.kteam.palm.domain.repository.impl.BaseDataRepositoryImpl;
import org.kteam.palm.model.AccessTime;

/**
 * @Package org.kteam.palm.domain.manager
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-06 17:04
 */
public class AccessTimeManager {
    private final Logger mLogger = Logger.getLogger(getClass());

    private AccessTimeRespository mAccessTimeRespository;

    public AccessTimeManager() {
        try {
            mAccessTimeRespository = new AccessTimeRepositoryImpl(BaseApplication.getContext());
        } catch (Exception e) {
            mLogger.error(e.getMessage(), e);
        }
    }

    public AccessTime getAccessTime(String categoryId) {
        AccessTime accessTime = mAccessTimeRespository.get(categoryId);
        if (accessTime == null) {
            accessTime = new AccessTime();
            accessTime.categoryId = categoryId;
            accessTime.time = "1970-12-12 12:12:12";
        }
        return accessTime;
    }

    public void updateAccessTime(AccessTime accessTime) {
        accessTime.time = DateUtils.getCurrentLongTime();
        mAccessTimeRespository.saveOrUpdate(accessTime);
    }
}
