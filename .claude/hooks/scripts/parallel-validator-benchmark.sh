#!/bin/bash

# POC: Parallel Validator Benchmark

# 가짜 validator (100ms 소요 시뮬레이션)
mock_validator() {
    local name="$1"
    sleep 0.1  # 100ms
    echo "[$name] validation complete"
}

echo "=== 순차 실행 벤치마크 ==="
start=$(python3 -c 'import time; print(int(time.time() * 1000))')

mock_validator "lombok-validator"
mock_validator "javadoc-validator"
mock_validator "aggregate-validator"
mock_validator "law-of-demeter-validator"
mock_validator "domain-testing-validator"

end=$(python3 -c 'import time; print(int(time.time() * 1000))')
sequential_time=$((end - start))
echo "순차 실행 시간: ${sequential_time}ms"

echo ""
echo "=== 병렬 실행 벤치마크 ==="
start=$(python3 -c 'import time; print(int(time.time() * 1000))')

mock_validator "lombok-validator" &
mock_validator "javadoc-validator" &
mock_validator "aggregate-validator" &
mock_validator "law-of-demeter-validator" &
mock_validator "domain-testing-validator" &
wait

end=$(python3 -c 'import time; print(int(time.time() * 1000))')
parallel_time=$((end - start))
echo "병렬 실행 시간: ${parallel_time}ms"

echo ""
echo "=== 성능 개선 결과 ==="
improvement=$(python3 -c "print(f'{(($sequential_time - $parallel_time) * 100 / $sequential_time):.1f}')")
echo "순차: ${sequential_time}ms"
echo "병렬: ${parallel_time}ms"
echo "개선율: ${improvement}%"
