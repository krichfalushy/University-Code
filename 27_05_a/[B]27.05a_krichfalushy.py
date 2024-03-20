from http.server import HTTPServer, CGIHTTPRequestHandler


HOST = ""
PORT = 80


if __name__ == "__main__":
    print(f"server -- http://localhost:{PORT}")
    http = HTTPServer((HOST, PORT), CGIHTTPRequestHandler)
    http.serve_forever()



