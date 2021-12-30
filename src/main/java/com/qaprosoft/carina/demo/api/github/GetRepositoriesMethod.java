package com.qaprosoft.carina.demo.api.github;

import com.qaprosoft.carina.core.foundation.api.AbstractApiMethodV2;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.R;

public class GetRepositoriesMethod extends AbstractApiMethodV2 {

    public GetRepositoriesMethod() {
        super();
        replaceUrlPlaceholder("base_url", Configuration.getEnvArg("api_url"));
        request.header("Authorization", R.CONFIG.get("github_access_token"));
    }
}