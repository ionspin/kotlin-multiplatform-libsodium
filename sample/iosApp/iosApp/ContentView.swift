import SwiftUI
import LibsodiumBindings


struct ContentView: View {
    var body: some View {
        
        Text("Hello, World! \(Sample.init().hashSomething())")
        
    }
}

func test() {
    
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
