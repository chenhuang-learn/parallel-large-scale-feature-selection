#! /usr/bin/env sh
./preprocess.py mushroom.data train_data test_data
hadoop fs -rm hc/train_data hc/test_data hc/base_model
hadoop fs -put train_data test_data base_model hc
rm -f train_data test_data
hadoop jar featsel_estimate.jar hc/train_data hc/base_model hc/new_model 1.0 10
hadoop fs -getmerge hc/new_model new_model
hadoop fs -rm -r hc/new_model
hadoop fs -put new_model hc
rm -f new_model
hadoop jar featsel_evaluate.jar hc/test_data hc/base_model hc/new_model hc/result 10
hadoop fs -getmerge hc/result result
hadoop fs -rm hc/new_model
hadoop fs -rm -r hc/result

