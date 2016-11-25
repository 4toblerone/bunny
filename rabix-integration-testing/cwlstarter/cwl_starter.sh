#!/bin/bash
virtualenv env/testenv
source env/testenv/bin/activate
pip install -e git+https://github.com/common-workflow-language/cwltest.git@master#egg=cwltest
cwltest --test conformance_test_v1.0.yaml --tool /home/travis/build/markosbg/debug/rabix-backend-local/target/rabix -j 4
