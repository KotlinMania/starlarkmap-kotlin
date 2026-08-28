import Testing
import StarlarkMap

@Suite struct StarlarkMapExportTests {
    @Test func testSwiftModuleLoads() throws {
        #expect(Bool(true), "StarlarkMap swift module imported cleanly")
    }
}
