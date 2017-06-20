package com.lhy.common.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "wx_counter")
public class WxCounter {
    @Id
    @Column(name = "counter_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long counterId;

    /**
     * 1:点赞数
     */
    @Column(name = "counter_type")
    private Long counterType;

    @Column(name = "counter_name")
    private String counterName;

    @Column(name = "counter_desc")
    private String counterDesc;

    @Column(name = "create_time")
    private Date createTime;

    private String state;

    private String remarks;

    /**
     * @return counter_id
     */
    public Long getCounterId() {
        return counterId;
    }

    /**
     * @param counterId
     */
    public void setCounterId(Long counterId) {
        this.counterId = counterId;
    }

    /**
     * 获取1:点赞数
     *
     * @return counter_type - 1:点赞数
     */
    public Long getCounterType() {
        return counterType;
    }

    /**
     * 设置1:点赞数
     *
     * @param counterType 1:点赞数
     */
    public void setCounterType(Long counterType) {
        this.counterType = counterType;
    }

    /**
     * @return counter_name
     */
    public String getCounterName() {
        return counterName;
    }

    /**
     * @param counterName
     */
    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    /**
     * @return counter_desc
     */
    public String getCounterDesc() {
        return counterDesc;
    }

    /**
     * @param counterDesc
     */
    public void setCounterDesc(String counterDesc) {
        this.counterDesc = counterDesc;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}