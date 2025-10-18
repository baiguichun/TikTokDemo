package com.xiaobai.data.repository.cameramedia

import com.xiaobai.data.model.TemplateModel
import com.xiaobai.data.source.TemplateDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TemplateRepository @Inject constructor() {
    fun getTemplates(): Flow<List<TemplateModel>> {
        return TemplateDataSource.fetchTemplates()
    }
}

//@Inject constructor()
//告诉 Dagger Hilt 这个类可以通过构造函数注入的方式创建实例
//允许依赖注入框架自动管理 TemplateRepository 的生命周期和实例化