package com.xiaobai.domain.cameramedia

import com.xiaobai.data.model.TemplateModel
import com.xiaobai.data.repository.cameramedia.TemplateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取模板列表
 *
 */
class GetTemplateUseCase @Inject constructor(
    //注入 TemplateRepository 依赖
    private val templateRepository: TemplateRepository
) {
    //
    operator fun invoke(): Flow<List<TemplateModel>> {
        return templateRepository.getTemplates()
    }
}

//设计模式
//采用 Use Case 模式：
//封装特定业务逻辑（获取模板）
//作为数据层和表示层之间的桥梁
//使用 Kotlin 的 invoke 操作符提供简洁的调用语法

//业务逻辑
//operator fun invoke(): 操作符函数，允许类实例像函数一样被调用
//返回类型: Flow<List<TemplateModel>>，提供响应式数据流
//调用 templateRepository.getTemplates() 获取模板数据