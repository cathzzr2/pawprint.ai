#!/bin/bash
set -x

awslocal s3 mb s3://virtual-pet-s3
awslocal sqs create-queue --queue-name virtual-pet-queue
set +x