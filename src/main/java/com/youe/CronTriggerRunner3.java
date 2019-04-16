package com.youe;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class CronTriggerRunner3 {
	SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	static Scheduler scheduler = null;
	public static void run () {
		try {
			JobDetail jobDetail = new JobDetail("job1_2", "jGroup1", quartz3.class);
			// ①-1：创建CronTrigger，指定组及名称
			CronTrigger cronTrigger = new CronTrigger("backupMysqlTrigger", "tgroup1");
			//CronExpression cexp = new CronExpression("0/5 * * * * ?");// ①-2：定义Cron表达式
			//1点55 开始 6小时一次
//			CronExpression cexp = new CronExpression("0 55 1/6 * * ?");// ①-2：定义Cron表达式
			CronExpression cexp = new CronExpression("0 0/1 17 * * ?");// ①-2：定义Cron表达式
//			CronExpression cexp = new CronExpression("0 17 16 * * ?");// ①-2：定义Cron表达式,每天1.30执行
			cronTrigger.setCronExpression(cexp);// ①-3：设置Cron表达式
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			Scheduler scheduler = schedulerFactory.getScheduler();
			scheduler.scheduleJob(jobDetail, cronTrigger);
			scheduler.start();
			// ②
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("导出数据错误！");
		}
	}
	public static void shutdown() {
		try {
			System.out.println("====shuwdown==========");
			scheduler.shutdown();
//			scheduler.deleteJob();
			System.out.println("=====ok=========");
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}