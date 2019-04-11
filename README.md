# MBOOG
==============
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.liyiorg/mboog/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.liyiorg/mboog/)
[![GitHub release](https://img.shields.io/github/release/liyiorg/mboog.svg)](https://github.com/liyiorg/mboog/releases)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)


&emsp;&emsp;MBOOG 是基于 [MyBatis generator](https://github.com/mybatis/generator) 实现的敏捷开发工具，释放开发中对于数据增、删、改、查所占用的时间，提高代码质量。解决原生工具中多种不足，是数据访问层快速开发、标准化开发的一大利器。

## 设计
&emsp;&emsp;MBOOG 包含两个模块。

* mboog-generator 代码生成模块
* mboog-support   支持模块

## 特点

* 简洁&emsp;&emsp;生成的代码整洁无冗余
* 易用&emsp;&emsp;学习使用成本低
* 通用&emsp;&emsp;包括mybatis 大部分使用场景
* 数据库支持&emsp;mysql, oracle, postgresql

## 特性
* 支持JSR310  使用java8 新日期包对应数据 date,datetime,time 等类型
* 支持按需（数据库字段 ）查询数据
* 支持灵活的WHERE条件拼接
* 支持分页查询
* 支持自动乐观锁
* 支持upsert(保存或更新)
* 支持生成service 层代码
 

## 文档

[wiki](https://github.com/liyiorg/mboog/wiki)