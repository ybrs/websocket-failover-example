import React, { Component } from 'react';
import './App.css';


class Client extends Component {
  constructor(props){
    super(props)
    this.state = {networkState: 'DISCONNECTED'}
    this.onOpen = this.onOpen.bind(this)
    this.onClose = this.onClose.bind(this)
    this.onMessage = this.onMessage.bind(this)
    this.onError = this.onError.bind(this)
    this.incrScore = this.incrScore.bind(this)
    this.reconnect = this.reconnect.bind(this)
    this.closeWebsocket = this.closeWebsocket.bind(this)

  }

  connectToRandomServer(){
    let self = this;
    fetch('http://localhost:9081/servers/')
    .then(function(response) {
        return response.json();
    })
    .then(function(servers) {
      let server = null;
      for (let k in servers){
        if (k.indexOf(':9081')===-1){
          console.log("-> server ->", k)
          server = k;
        }
      }
      self.connectToServer(server)
    });
}

  componentDidMount(){
    if (this.props.socketUrl){
      this.connectToServer(this.props.socketUrl)
    } else {
      this.connectToRandomServer()
    }

  }

  incrScore(){
    this.ws.send("INCR_SCORE")
  }

  onOpen(evt){
    this.setState({networkState: 'CONNECTED'})
    this.ws.send("JOIN_ROOM")
  }

  onClose(evt){
    this.setState({networkState: 'DISCONNECTED'})
  }

  onMessage(evt){
    console.log("message:", this.serverUrl, evt.data);
    try {
      let d = JSON.parse(evt.data)
      this.setState({score: d.score})
    } catch(e){
      // console.log(e) // unimportant
    }
  }

  onError(evt){
    console.log("onerror", evt)
  }

  closeWebsocket(){
    this.ws.close()
  }

  reconnect(){
    this.connectToServer(this.serverUrl)
  }

  connectToServer(serverUrl){
    this.setState({networkState: 'CONNECTING: '+this.props.socketUrl})
    this.serverUrl = serverUrl;
    console.log("connecting to -> ", serverUrl)

    this.ws = new WebSocket(serverUrl);
    this.ws.onopen = this.onOpen;
    this.ws.onmessage = this.onMessage;
    this.ws.onclose = this.onClose;
    this.ws.onerror = this.onError;
}


  render(){
    return <div>
      <div id="score">
      <h1>{this.props.name}</h1>
      Score:{this.state.score}</div>
      <button onClick={this.incrScore}>Incr Score</button>|
      <ConnectButton networkState={this.state.networkState}
        reconnect={this.reconnect}
        closeWebsocket={this.closeWebsocket}
      />
      <div id="state">{this.state.networkState} | {this.serverUrl}</div>
    </div>
  }
}

let ConnectButton = ({networkState, reconnect, closeWebsocket})=>{
  if (networkState === 'DISCONNECTED'){
    return <button onClick={reconnect}>reconnect</button>
  }

  return <button onClick={closeWebsocket}>Close Socket</button>
}

class App extends Component {
  render() {
    return (
      <div className="App">

        <Client name="client1" socketUrl="ws://localhost:9081/events/" />
        <hr/>
        <Client name="client2" socketUrl="ws://localhost:9082/events/" />
        <hr/>
        <Client name="client3" socketUrl="ws://localhost:9083/events/" />
        <hr/>
        <Client name="client4 random" socketUrl="" />

      </div>
    );
  }
}

export default App;
