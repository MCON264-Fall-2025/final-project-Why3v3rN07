import xml.etree.ElementTree as ET
import sys
import os

def check_coverage():
    report_path = 'target/site/jacoco/jacoco.xml'

    if not os.path.exists(report_path):
        print("❌ JaCoCo report not found. Ensure tests are running with coverage.")
        sys.exit(1)

    tree = ET.parse(report_path)
    root = tree.getroot()

    # Extract coverage metrics
    counters = root.findall('.//counter[@type="INSTRUCTION"]')

    total_covered = 0
    total_missed = 0

    for counter in counters:
        total_covered += int(counter.get('covered', 0))
        total_missed += int(counter.get('missed', 0))

    total = total_covered + total_missed
    coverage_percentage = (total_covered / total * 100) if total > 0 else 0

    print(f"Code Coverage: {coverage_percentage:.2f}%")

    # Check for student test files
    test_count = 0
    for package in root.findall('.//package'):
        for sourcefile in package.findall('.//sourcefile'):
            filename = sourcefile.get('name', '')
            if filename.endswith('Test.java') and not filename.startswith('Venue') and not filename.startswith('Guest') and not filename.startswith('Seating') and not filename.startswith('Task'):
                test_count += 1

    print(f"Student test files found: {test_count}")

    if coverage_percentage >= 60 and test_count >= 3:
        print("✅ Coverage and test requirements met")
        sys.exit(0)
    else:
        print("❌ Insufficient coverage or test files")
        sys.exit(1)

if __name__ == '__main__':
    check_coverage()
