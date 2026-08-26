#if canImport(Testing)
import Testing
import Anstyle

@Suite("Anstyle Swift Export Tests")
struct AnstyleExportTests {
    @Test("Anstyle swift module imported cleanly")
    func testSwiftModuleLoads() throws {
        #expect(Bool(true), "Anstyle swift module imported cleanly")
    }
}
#elseif canImport(XCTest)
import XCTest
import Anstyle

final class AnstyleExportTests: XCTestCase {
    func testSwiftModuleLoads() throws {
        XCTAssertTrue(true, "Anstyle swift module imported cleanly")
    }
}
#endif
