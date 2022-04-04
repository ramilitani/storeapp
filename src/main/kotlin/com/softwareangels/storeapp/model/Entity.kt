package com.softwareangels.storeapp.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Cluster(
    var name: String,
    @Id @GeneratedValue var id: Long? = null)

@Entity
class Region(
    var name: String,
    var type: String,
    @ManyToOne @JoinColumn(name = "cluster_id") var cluster: Cluster,
    @Id @GeneratedValue var id: Long? = null)

@Entity
class Store(
    var name: String,
    var theme: String,
    @ManyToOne @JoinColumn(name = "region_id") var region: Region,
    @Id @GeneratedValue var id: Long? = null)

@Entity
class Product(
    var cluster: String?,
    var region: String?,
    var regionType: String?,
    var storeName: String?,
    var storeTheme: String?,
    var productModel: String?,
    var productSize: String?,
    var productSku: String?,
    var productEan: Long?,
    var productDesc: String?,
    var productSeason: String?,
    @Id @GeneratedValue var id: Long? = null)

@Entity
class AvailableField(
    var name: String,
    var dbFieldName: String,
    @Id @GeneratedValue var id: Long? = null)

