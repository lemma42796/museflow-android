package com.ixuea.courses.mymusic.component.login.model

import com.ixuea.courses.mymusic.model.Base

/**
 * 登录成功后，返回的信息
 */
class Session(
    var userId: String? = null,
    var session: String? = null,
    var chatToken: String? = null,
) : Base()
