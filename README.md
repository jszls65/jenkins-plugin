# 项目介绍

## 简介
> Jenkins插件服务, 读取Jenkins的构建结果, 发送到钉钉群机器人.

## 实现思路
1. Jenkins构建结束后, 执行curl shell脚本, 通过http请求本项目提供的Rest服务
1. 服务根据项目名称解析Jenkins执行完成后的文件
1. 如果构建成功, 则将构建基本信息通过钉钉API发送给钉钉群聊机器人
1. 如果构建失败, 再将git最后一次提交人\commit注释\提交时间等信息, 和console报错日志截图发送给钉钉群聊机器人.

## 技术栈
1. Springboot 2.x
1. Java 文本转图片
1. 七牛云SDK
1. 钉钉群聊机器人SDK
