package com.sum.main.ui.mine.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.sum.common.model.ArticleInfo
import com.sum.framework.toast.TipsToast
import com.sum.network.callback.IApiErrorCallback
import com.sum.network.manager.ApiManager
import com.sum.network.viewmodel.BaseViewModel

/**
 * @author mingyan.su
 * @date   2023/3/3 8:22
 * @desc   我的ViewModel
 */
class MineViewModel : BaseViewModel() {
    val recommendLiveData: MutableLiveData<MutableList<ArticleInfo>?> = MutableLiveData()

    /**
     * 首页推荐列表
     * @param count 页码
     * @param pageSize 每页数量
     */
    fun getRecommendList(count: Int, pageSize: Int = 20): LiveData<MutableList<ArticleInfo>?> {
        launchUIWithResult(responseBlock = {
            ApiManager.api.getHomeList(count, pageSize)
        }, errorCall = object : IApiErrorCallback {
            override fun onError(code: Int?, error: String?) {
                TipsToast.showTips(error)
                recommendLiveData.value = null
            }
        }, successBlock = {
            if (it == null || it.datas.isEmpty()) {
                recommendLiveData.value = null
            } else {
                recommendLiveData.value = it.datas
            }
        })
        return recommendLiveData
    }

    /**
     * 收藏 or 取消收藏站内文章
     * @param id  文章id
     * @param isCollect 是否收藏
     */
    fun collectArticle(id: Int, isCollect: Boolean): LiveData<Boolean> {
        return liveData {
            val data = safeApiCallWithResult(errorCall = object : IApiErrorCallback {
                override fun onError(code: Int?, error: String?) {
                    super.onError(code, error)
                }
            }) {
                if (!isCollect) {
                    //收藏站内文章
                    ApiManager.api.collectArticle(id)
                } else {
                    //取消收藏站内文章
                    ApiManager.api.cancelCollectArticle(id)
                }
            }
            emit(isCollect)
        }
    }
}