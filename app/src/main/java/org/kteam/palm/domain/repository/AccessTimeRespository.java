package org.kteam.palm.domain.repository;

import org.kteam.palm.model.AccessTime;

/**
 * @Package org.kteam.palm.domain.repository
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-06 16:52
 */
public interface AccessTimeRespository extends BaseRepository {
    public void saveOrUpdate(AccessTime accessTime);

    public AccessTime get(String categoryId);
}
