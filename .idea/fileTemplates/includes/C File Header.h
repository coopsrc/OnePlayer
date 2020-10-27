#parse("License.txt")
#if ($HEADER_COMMENTS)
//
// Created by $USER_NAME on ${YEAR}-${MONTH}-${DAY} ${HOUR}:${MINUTE}.
#if ($ORGANIZATION_NAME && $ORGANIZATION_NAME != "")
// Copyright (c) $YEAR ${ORGANIZATION_NAME}#if (!$ORGANIZATION_NAME.endsWith(".")).#end All rights reserved.
#end
//
#end

