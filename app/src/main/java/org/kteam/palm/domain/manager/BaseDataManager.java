package org.kteam.palm.domain.manager;

import android.widget.Toast;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.domain.repository.BaseDataRepository;
import org.kteam.palm.domain.repository.impl.BaseDataRepositoryImpl;
import org.kteam.palm.model.BaseData;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.BaseDataResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * @Package org.kteam.palm.network
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-13 22:34
 */
public class BaseDataManager {

    private final Logger mLogger = Logger.getLogger(getClass());

    private BaseDataRepository mBaseDataRepository;

    public BaseDataManager() {
        try {
            mBaseDataRepository = new BaseDataRepositoryImpl(BaseApplication.getContext());
        } catch (Exception e) {
            mLogger.error(e.getMessage(), e);
        }
    }

    public void loadBaseData() {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("timeStamp", String.valueOf("111"));
        String token = NetworkUtils.getToken(BaseApplication.getContext(), paramMap, Constants.URL_GET_DICT);
        paramMap.put("token", token);

        RequestClient<BaseDataResponse> requestClient = new RequestClient<BaseDataResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<BaseDataResponse>() {
            @Override
            public void onLoadComplete(BaseDataResponse response) {
                if (response.code == 0 && response.body != null) {
                    mBaseDataRepository.saveList(response.body);
                } else {
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                    mLogger.error("base data is null");
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(BaseApplication.getContext().getString(R.string.network_error));
            }
        });
        requestClient.executePost(BaseApplication.getContext(),
                "",
                Constants.BASE_URL + Constants.URL_GET_DICT,
                BaseDataResponse.class,
                paramMap);
    }

    /**月份列表*/
    public List<BaseData> getMonthList(String startMonth) {
        int start = 0;
        try {
            start = Integer.parseInt(startMonth);
        } catch (Exception e) {
            mLogger.error(e.getMessage(), e);
        }
        List<BaseData> list =  mBaseDataRepository.getMonthList();
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                BaseData data = list.get(i);
                if (Integer.valueOf(data.value) < start) {
                    list.remove(i);
                }
            }
        }
        Collections.sort(list, new Comparator<BaseData>() {
            @Override
            public int compare(BaseData lhs, BaseData rhs) {
                try {
                    int left = Integer.parseInt(lhs.value);
                    int right = Integer.parseInt(rhs.value);
                    if (left > right) {
                        return 1;
                    } else if (left < right) {
                        return -1;
                    }
                } catch (Exception e) {
                    mLogger.debug(e.getMessage(), e);
                }
                return 0;
            }
        });
        return list;
    }

    /**月份列表*/
    public List<BaseData> getMonthList() {

        List<BaseData> list =  mBaseDataRepository.getMonthList();
        if (list == null) return null;
        Collections.sort(list, new Comparator<BaseData>() {
            @Override
            public int compare(BaseData lhs, BaseData rhs) {
                try {
                    int left = Integer.parseInt(lhs.value);
                    int right = Integer.parseInt(rhs.value);
                    if (left > right) {
                        return 1;
                    } else if (left < right) {
                        return -1;
                    }
                } catch (Exception e) {
                    mLogger.debug(e.getMessage(), e);
                }
                return 0;
            }
        });
        return list;
    }

    /**缴费档次*/
    public List<BaseData> getPayedLevel() {
        List<BaseData> levelList = mBaseDataRepository.getPayedLevel();
        if (levelList != null) {
            Collections.sort(levelList, new Comparator<BaseData>() {
                @Override
                public int compare(BaseData lhs, BaseData rhs) {
                    try {
                        int left = Integer.parseInt(lhs.value);
                        int right = Integer.parseInt(rhs.value);
                        if (left > right) {
                            return  1;
                        } else if (left < right) {
                            return -1;
                        }
                    } catch (Exception e) {
                        mLogger.debug(e.getMessage(), e);
                    }
                    return 0;
                }
            });
        }
        return levelList;
    }

    /**缴费档次*/
    public List<BaseData> getPayedLevel(boolean high) {
        List<BaseData> levelList = mBaseDataRepository.getPayedLevel(high ? Constants.PAY_HIGH_LEVEL_KEY : Constants.PAY_LOW_LEVEL_KEY);
        if (levelList != null) {
            Collections.sort(levelList, new Comparator<BaseData>() {
                @Override
                public int compare(BaseData lhs, BaseData rhs) {
                    try {
                        int left = Integer.parseInt(lhs.value);
                        int right = Integer.parseInt(rhs.value);
                        if (left > right) {
                            return  1;
                        } else if (left < right) {
                            return -1;
                        }
                    } catch (Exception e) {
                        mLogger.debug(e.getMessage(), e);
                    }
                    return 0;
                }
            });
        }
        return levelList;
    }

    /**医疗保险新参保缴费档次*/
    public List<BaseData> getYiliaoInsuredPayedLevel() {
        List<BaseData> levelList = mBaseDataRepository.getYiliaoInsuredPayedLevel();
        if (levelList != null) {
            Collections.sort(levelList, new Comparator<BaseData>() {
                @Override
                public int compare(BaseData lhs, BaseData rhs) {
                    try {
                        int left = Integer.parseInt(lhs.value);
                        int right = Integer.parseInt(rhs.value);
                        if (left > right) {
                            return  1;
                        } else if (left < right) {
                            return -1;
                        }
                    } catch (Exception e) {
                        mLogger.debug(e.getMessage(), e);
                    }
                    return 0;
                }
            });
        }
        return levelList;
    }

    /**获取民族列表*/
    public List<BaseData> getNationList() {
        List<BaseData> list = mBaseDataRepository.getNationList();
        BaseData hanNation = null;
        for (BaseData baseData : list) {
            if ("汉".equals(baseData.label) || "汉族".equals(baseData.label)) {
                hanNation = baseData;
                break;
            }
        }
        if (hanNation != null) {
            list.remove(hanNation);
            list.add(0, hanNation);
        }
        return list;
    }


    /**获取银行列表*/
    public List<BaseData> getPayBankList() {
        List<BaseData> backList = mBaseDataRepository.getPayBankList();
        if (backList != null) {
            Collections.sort(backList, new Comparator<BaseData>() {
                @Override
                public int compare(BaseData lhs, BaseData rhs) {
                    try {
                        if (lhs.sort > rhs.sort) {
                            return  1;
                        } else if (lhs.sort < rhs.sort) {
                            return -1;
                        }
                    } catch (Exception e) {
                        mLogger.debug(e.getMessage(), e);
                    }
                    return 0;
                }
            });
        }
        return backList;
    }

    public List<BaseData> getPayStatusList() {
        List<BaseData> list = mBaseDataRepository.getPayStatusList();
        return list;
    }
}
