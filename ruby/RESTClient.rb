class RESTClient

  def initialize(apiUsername, apiPasswordHash, apiHost, apiBase)
    @apiUsername = apiUsername
    @apiPasswordHash = apiPasswordHash
    @apiHost = apiHost
    @apiBase = apiBase
  end

  def doGet(resource_path)
    return doRequest(resource_path, "", 'Get')
  end

  def doPost(resource_path, xml)
    doRequest(resource_path, xml, 'Post')
  end

  def doPut(resource_path, xml)
    doRequest(resource_path, xml, 'Put')
  end

  private

  def doRequest(resource_path, xml, method)
    https = Net::HTTP.new(@apiHost, Net::HTTP.https_default_port)
    https.use_ssl = true

    https.start() { |http|
      case method
        when 'Put'
          req = Net::HTTP::Put.new(@apiBase + resource_path)
        when 'Post'
          req = Net::HTTP::Post.new(@apiBase + resource_path)
        else
          req = Net::HTTP::Get.new(@apiBase + resource_path)
      end

      req.basic_auth(@apiUsername, @apiPasswordHash)
      req.body = xml
      req.content_type = "application/xml"

      res = http.request(req)

      case res
        when Net::HTTPSuccess, Net::HTTPRedirection
          puts "#{method} OK (#{resource_path})"
          case method
            when'Get'
              return res.body
            when 'Post'
              return res["location"]
          end
        else
          puts res.body
          res.error!
      end
    }
  end
end
